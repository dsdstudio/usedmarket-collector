package net.dsdstudio.usedmarket.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * usedmarket-collector net.dsdstudio.usedmarket.services
 *
 * @author : bhkim
 * @since : 2015-01-27 20:30
 */
@Service
public class MessageService {
    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private ClienBoardDataGrabService clienBoardDataGrabService;

    @Autowired
    private SlrBoardDataGrabService slrBoardDataGrabService;

    @Scheduled(fixedRate = 5000)
    public void crawling() {
        this.clienBoardDataGrabService.boardData().forEach(System.out::println);
        //this.slrBoardDataGrabService.boardData().forEach(System.out::println);
    }
}