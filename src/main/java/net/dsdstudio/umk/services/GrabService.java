package net.dsdstudio.umk.services;

import net.dsdstudio.umk.BoardData;

import java.util.stream.Stream;

/**
 * Board데이터 수집기 인터페이스
 */
public interface GrabService {
    public static final String UserAgentStr = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36";

    void login();

    Boolean isLogined();

    Stream<BoardData> boardData();
}
