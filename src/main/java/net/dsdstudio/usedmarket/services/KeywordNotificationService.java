package net.dsdstudio.usedmarket.services;

import net.dsdstudio.usedmarket.BoardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
    private GrabService clienBoardDataGrabService;

    @Autowired
    private GrabService slrBoardDataGrabService;
    @Autowired
    private GrabService twoCpuDataGrabService;

    @Autowired
    private SlackNotifier slackNotifier;

    private Notifier notifier;

    @PostConstruct
    public void setUp() {
        notifier = new Notifier(this.twoCpuDataGrabService);
    }

    class Notifier {
        private Integer counter = 0;
        private GrabService service;

        public Notifier(GrabService service) {
            this.service = service;
        }

        public void monitoring(List<String> keywords) {
            List<BoardData> list = service.boardData()
                    .filter(b -> b.id > counter)
                    .collect(Collectors.toList());
            if (list.isEmpty()) return;

            list.stream()
                    .filter(b -> keywords.stream().anyMatch(keyword -> b.subject.contains(keyword)))
                    .map(SlackNotifier.Message::new)
                    .forEach(slackNotifier::notifyMessage);

            this.counter = list.stream()
                    .map(BoardData::getId)
                    .reduce(Integer::max).get();
            logger.debug(list.stream().findFirst().get().toString());
        }
    }

    @Scheduled(fixedRate = 30 * 1000)
    public void sendKeywordNotification() {
        List<String> keywords = Arrays.asList("DDR", "ddr", "램", "TB", "tb", "SATA", "HDD", "ECC", "ecc");
        this.notifier.monitoring(keywords);
        logger.debug("monitoring keyword => " + keywords.stream().collect(Collectors.joining(",")));
    }
}
