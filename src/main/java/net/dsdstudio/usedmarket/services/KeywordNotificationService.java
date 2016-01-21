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

    @Autowired
    private SlackNotifier slackNotifier;

    private Integer maxSlrclubBoardId = 0;
    private Integer maxClienBoardId = 0;


    public void monitoring(String keyword) {
        List<BoardData> list = clienBoardDataGrabService.boardData()
                .filter(b -> b.id > this.maxClienBoardId)
                .collect(Collectors.toList());
        if (list.isEmpty()) return;

        list.stream().filter(b -> b.subject.contains(keyword))
                .forEach(b -> slackNotifier.notifyMessage(SlackNotifier.CHANNEL_NOTIFY, "[" + b.date + "] " + b.subject + "\nhttp://www.clien.net" + b.detailUrl));

        Integer maxClienBoardId = list.stream().map(board -> board.id).reduce(Integer::max).get();
        this.maxClienBoardId = maxClienBoardId;
    }

    public void slrMonitoring(String keyword) {
        List<BoardData> list = slrBoardDataGrabService.boardData()
                .filter(b -> b.id > this.maxSlrclubBoardId)
                .collect(Collectors.toList());
        if (list.isEmpty()) return;

        list.stream().filter(b -> b.subject.contains(keyword))
                .forEach(b -> slackNotifier.notifyMessage(SlackNotifier.CHANNEL_NOTIFY, "[" + b.date + "] " + b.subject + "\nhttp://www.slrclub.com" + b.detailUrl));

        Integer maxSlrclubBoardId = list.stream().map(board -> board.id).reduce(Integer::max).get();
        this.maxSlrclubBoardId = maxSlrclubBoardId;
    }

    @Scheduled(fixedRate = 30 * 1000)
    public void sendKeywordNotification() {
        String keyword = "a7";
        monitoring(keyword);
        slrMonitoring(keyword);
    }

}