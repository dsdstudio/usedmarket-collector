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

/**
 * BoardData 생성 역할을 담당하는 Factory Class
 */
@Service
public class BoardDataProvider {
    public enum BoardType {
        SLR, CLIEN
    }

    public BoardData getInstance(BoardType $boardType, Elements $tds) {
        if ($boardType == BoardType.SLR) {
            BoardData o = new BoardData();
            o.id = Integer.valueOf($tds.select(".list_num").html(), 10);
            o.detailUrl = $tds.select(".sbj").select("a").attr("href");
            o.subject = $tds.select(".sbj").select("a").text();
            o.ownerName = $tds.select(".list_name").select("span").text();
            o.date = $tds.select(".list_date").text();
            o.dataType = BoardType.SLR;
            return o;
        } else if ($boardType == BoardType.CLIEN) {
            BoardData o = new BoardData();
            o.id = Integer.valueOf($tds.get(0).html(), 10);
            o.detailUrl = $tds.select(".post_subject").select("a").attr("href");
            o.subject = $tds.select(".post_subject").text();
            o.ownerName = $tds.select(".post_name").select("a").attr("title");
            o.date = $tds.get(4).select("span").attr("title");
            o.dataType = BoardType.CLIEN;
            return o;
        } else {
            throw new IllegalArgumentException("잘못된 인자입니다. ");
        }

    }
}
