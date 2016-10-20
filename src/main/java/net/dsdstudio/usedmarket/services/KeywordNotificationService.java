package net.dsdstudio.usedmarket.services;

import net.dsdstudio.usedmarket.BoardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ClienBoardDataGrabService clienBoardDataGrabService;

    @Autowired
    private SlrBoardDataGrabService slrBoardDataGrabService;
    @Autowired
    private TwoCpuDataGrabService twoCpuDataGrabService;

    @Autowired
    private SlackNotifier slackNotifier;

    class Notifier {
        Integer counter = 0;
        GrabService service;

        public Notifier(GrabService service) {
            this.service = service;
        }

        public void monitoring(List<String> keywords) {
            List<BoardData> list = service.boardData()
                    .filter(b -> b.id > this.counter)
                    .collect(Collectors.toList());
            if (list.isEmpty()) return;

            list.stream().filter(b -> keywords.stream().anyMatch(keyword -> b.subject.contains(keyword)))
                    .forEach(b -> slackNotifier.notifyMessage(SlackNotifier.CHANNEL_NOTIFY, b.dataType + " [" + b.date + "] " + b.subject + "\n" + b.getDetailUrl()));

            this.counter = list.stream().map(board -> board.id).reduce(Integer::max).get();
            logger.debug(list.stream().findFirst().get().toString());
        }
    }

    @Scheduled(fixedRate = 30 * 1000)
    public void sendKeywordNotification() {
        List<String> keywords = Arrays.asList("420", "440");
        Notifier notifier = new Notifier(twoCpuDataGrabService);
        notifier.monitoring(keywords);
        logger.debug("monitoring keyword => " + keywords.stream().collect(Collectors.joining(",")));
    }
}