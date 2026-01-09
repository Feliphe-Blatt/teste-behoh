package com.behoh.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler para limpeza automática de reservas expiradas.
 * Executa a cada 5 minutos.
 */
@Component
public class ReservaScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(ReservaScheduler.class);
    
    private final ReservaService reservaService;
    
    public ReservaScheduler(ReservaService reservaService) {
        this.reservaService = reservaService;
    }
    
    /**
     * Limpa reservas expiradas a cada 5 minutos.
     * Cron: 0 */5 * * * * = A cada 5 minutos
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void limparReservasExpiradas() {
        logger.info("Iniciando limpeza de reservas expiradas...");
        
        try {
            int removidas = reservaService.limparReservasExpiradas();
            
            if (removidas > 0) {
                logger.info("Limpeza concluída: {} reserva(s) expirada(s) removida(s)", removidas);
            } else {
                logger.debug("Nenhuma reserva expirada encontrada");
            }
        } catch (Exception e) {
            logger.error("Erro ao limpar reservas expiradas", e);
        }
    }
}

