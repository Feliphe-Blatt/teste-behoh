@echo off
echo ========================================
echo  CORRIGINDO PROBLEMA DAS TABELAS
echo ========================================
echo.

echo [1/4] Criando tabela USUARIOS...
docker exec eventos-postgres psql -U postgres -d eventos_db -c "CREATE TABLE IF NOT EXISTS usuarios (id BIGSERIAL PRIMARY KEY, nome VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL UNIQUE, created_at TIMESTAMP NOT NULL DEFAULT NOW(), updated_at TIMESTAMP NOT NULL DEFAULT NOW());" >nul 2>&1
if %errorlevel% equ 0 (
    echo    OK - Tabela usuarios criada
) else (
    echo    AVISO - Pode ja existir
)

echo [2/4] Criando tabela EVENTOS...
docker exec eventos-postgres psql -U postgres -d eventos_db -c "CREATE TABLE IF NOT EXISTS eventos (id BIGSERIAL PRIMARY KEY, nome VARCHAR(255) NOT NULL, descricao TEXT, vagas INTEGER NOT NULL, vagas_disponiveis INTEGER NOT NULL, data_inicio TIMESTAMP NOT NULL, data_fim TIMESTAMP NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT NOW(), updated_at TIMESTAMP NOT NULL DEFAULT NOW(), CONSTRAINT vagas_positivas CHECK (vagas ^> 0), CONSTRAINT vagas_disponiveis_validas CHECK (vagas_disponiveis ^>= 0 AND vagas_disponiveis ^<= vagas));" >nul 2>&1
if %errorlevel% equ 0 (
    echo    OK - Tabela eventos criada
) else (
    echo    AVISO - Pode ja existir
)

echo [3/4] Criando tabela INSCRICOES...
docker exec eventos-postgres psql -U postgres -d eventos_db -c "CREATE TABLE IF NOT EXISTS inscricoes (id BIGSERIAL PRIMARY KEY, usuario_id BIGINT NOT NULL, evento_id BIGINT NOT NULL, data_inscricao TIMESTAMP NOT NULL DEFAULT NOW(), status VARCHAR(50) NOT NULL DEFAULT 'CONFIRMADA', FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE, FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE, UNIQUE (usuario_id, evento_id));" >nul 2>&1
if %errorlevel% equ 0 (
    echo    OK - Tabela inscricoes criada
) else (
    echo    AVISO - Pode ja existir
)

echo [4/4] Criando tabela RESERVAS...
docker exec eventos-postgres psql -U postgres -d eventos_db -c "CREATE TABLE IF NOT EXISTS reservas (id BIGSERIAL PRIMARY KEY, usuario_id BIGINT NOT NULL, evento_id BIGINT NOT NULL, data_reserva TIMESTAMP NOT NULL DEFAULT NOW(), data_expiracao TIMESTAMP NOT NULL, FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE, FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE);" >nul 2>&1
if %errorlevel% equ 0 (
    echo    OK - Tabela reservas criada
) else (
    echo    AVISO - Pode ja existir
)

echo.
echo Criando indices...
docker exec eventos-postgres psql -U postgres -d eventos_db -c "CREATE INDEX IF NOT EXISTS idx_inscricoes_usuario ON inscricoes(usuario_id);" >nul 2>&1
docker exec eventos-postgres psql -U postgres -d eventos_db -c "CREATE INDEX IF NOT EXISTS idx_inscricoes_evento ON inscricoes(evento_id);" >nul 2>&1
docker exec eventos-postgres psql -U postgres -d eventos_db -c "CREATE INDEX IF NOT EXISTS idx_reservas_expiracao ON reservas(data_expiracao);" >nul 2>&1
echo    OK - Indices criados

echo.
echo ========================================
echo  Verificando tabelas criadas...
echo ========================================
docker exec eventos-postgres psql -U postgres -d eventos_db -c "\dt"

echo.
echo ========================================
echo  Reiniciando API...
echo ========================================
docker-compose restart app
echo Aguarde 20 segundos para a API iniciar...
timeout /t 20 /nobreak >nul

echo.
echo ========================================
echo  CONCLUIDO!
echo ========================================
echo.
echo Agora teste no Insomnia:
echo   GET  http://localhost:8080/api/usuarios
echo   POST http://localhost:8080/api/usuarios
echo.
pause

