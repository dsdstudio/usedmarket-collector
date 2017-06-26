package net.dsdstudio.usedmarket.services;

/**
 * usedmarket-collector net.dsdstudio.usedmarket.services
 *
 * @author : bhkim
 * @since : 2015-01-22 오후 10:40
 */

import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * BoardData 생성 역할을 담당하는 Factory Class
 */
@Service
public class BoardDataProvider {
    public enum BoardType {
        SLR, TWOCPU, CLIEN
    }

    private static final Map<BoardType, Function<Elements, BoardData>> commandMap = new HashMap<>();
    @PostConstruct
    private void setUp() {
        commandMap.put(BoardType.CLIEN, $tds -> BoardData.builder()
                .id(Integer.valueOf($tds.get(0).html(), 10))
                .detailUrl("http://www.clien.net" + $tds.select(".post_subject").select("a").attr("href"))
                .subject($tds.select(".post_subject").text())
                .ownerName($tds.select(".post_name").select("a").attr("title"))
                .date($tds.get(4).select("span").attr("title"))
                .dataType(BoardType.CLIEN)
                .build());
        commandMap.put(BoardType.SLR, $tds -> BoardData.builder()
                .id(Integer.valueOf($tds.select(".list_num").html(), 10))
                .detailUrl("http://www.slrclub.com" + $tds.select(".sbj").select("a").attr("href"))
                .subject($tds.select(".sbj").select("a").text())
                .ownerName($tds.select(".list_name").select("span").text())
                .date($tds.select(".list_date").text())
                .dataType(BoardType.SLR)
                .build());
        commandMap.put(BoardType.TWOCPU, $tds -> {
            BoardData o = BoardData.builder()
                    .id(Integer.valueOf($tds.get(0).select("span").text()))
                    .subject($tds.get(1).select("a span").text())
                    .ownerName($tds.get(2).select("a").text())
                    .date($tds.get(3).text())
                    .dataType(BoardType.TWOCPU)
                    .build();
            String detailUrl = $tds.get(1).select("a").attr("href");
            o.setDetailUrl("http://2cpu.co.kr" + detailUrl.substring(2, detailUrl.length()));
            return o;
        });
    }

    public BoardData getInstance(BoardType $boardType, Elements $tds) {
        Function<Elements, BoardData> fn = commandMap.get($boardType);
        if (fn == null) throw new IllegalArgumentException("잘못된 인자입니다. ");
        return fn.apply($tds);
    }
}
