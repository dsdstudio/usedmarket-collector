package net.dsdstudio.usedmarket.services;

/**
 * usedmarket-collector net.dsdstudio.usedmarket.services
 *
 * @author : bhkim
 * @since : 2015-01-22 오후 10:40
 */

import net.dsdstudio.usedmarket.BoardData;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

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

    static {
        commandMap.put(BoardType.CLIEN, $tds -> {
            BoardData o = new BoardData();
            o.id = Integer.valueOf($tds.get(0).html(), 10);
            o.detailUrl = "http://www.clien.net" + $tds.select(".post_subject").select("a").attr("href");
            o.subject = $tds.select(".post_subject").text();
            o.ownerName = $tds.select(".post_name").select("a").attr("title");
            o.date = $tds.get(4).select("span").attr("title");
            o.dataType = BoardType.CLIEN;
            return o;
        });
        commandMap.put(BoardType.SLR, $tds -> {
            BoardData o = new BoardData();
            o.id = Integer.valueOf($tds.select(".list_num").html(), 10);
            o.detailUrl = "http://www.slrclub.com" + $tds.select(".sbj").select("a").attr("href");
            o.subject = $tds.select(".sbj").select("a").text();
            o.ownerName = $tds.select(".list_name").select("span").text();
            o.date = $tds.select(".list_date").text();
            o.dataType = BoardType.SLR;
            return o;
        });
        commandMap.put(BoardType.TWOCPU, $tds -> {
            BoardData o = new BoardData();
            o.id = Integer.valueOf($tds.get(0).select("span").text());
            o.detailUrl = $tds.get(1).select("a").attr("href");
            o.detailUrl = "http://2cpu.co.kr" + o.detailUrl.substring(2, o.detailUrl.length());
            o.subject = $tds.get(1).select("a span").text();
            o.ownerName = $tds.get(2).select("a").text();
            o.date = $tds.get(3).text();
            o.dataType = BoardType.TWOCPU;
            return o;
        });
    }


    public BoardData getInstance(BoardType $boardType, Elements $tds) {
        Function<Elements, BoardData> fn = commandMap.get($boardType);
        if (fn == null) throw new IllegalArgumentException("잘못된 인자입니다. ");
        return fn.apply($tds);
    }
}
