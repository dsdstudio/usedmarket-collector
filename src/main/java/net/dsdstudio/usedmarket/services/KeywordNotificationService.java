package net.dsdstudio.usedmarket.services;

import net.dsdstudio.usedmarket.BoardData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * usedmarket-collector net.dsdstudio.usedmarket.services
 *
 * @author : bhkim
 * @since : 2015-01-27 20:30
 */
@Service
public class KeywordNotificationService {
    @Autowired
    private ClienBoardDataGrabService clienBoardDataGrabService;

    @Autowired
    private SlrBoardDataGrabService slrBoardDataGrabService;

    private Integer maxSlrclubBoardId = 0;
    private Integer maxClienBoardId = 0;

    @Scheduled(fixedRate = 30 * 1000)
    public void sendKeywordNotification() {
        this.clienBoardDataGrabService.boardData().forEach(System.out::println);
        List<BoardData> list = this.slrBoardDataGrabService.boardData()
                .filter(b -> b.id > this.maxSlrclubBoardId)
                .collect(Collectors.toList());
        if (list.isEmpty()) return;

        list.stream().forEach(System.out::println);
        Integer maxId = list.stream().map(board -> board.id).reduce(Integer::max).get();
        if (maxId == this.maxSlrclubBoardId) return;

        this.maxSlrclubBoardId = maxId;

        System.out.println(this.maxSlrclubBoardId);
    }
}