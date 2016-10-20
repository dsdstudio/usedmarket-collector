package net.dsdstudio.usedmarket.services;

import net.dsdstudio.usedmarket.BoardData;
import net.dsdstudio.usedmarket.utils.Util;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * Created by bhkim on 2016. 10. 21..
 */
@Service
public class TwoCpuDataGrabService implements GrabService {
    @Value("${2cpu.id}")
    private String id;
    @Value("${2cpu.pwd}")
    private String pwd;

    private final String loginUrl = "http://2cpu.co.kr/bbs/login_check.php";
    private final String bbsUrl = "http://2cpu.co.kr/sell?bo_table=sell&sca=&sfl=wr_subject&stx=";
    private final String paramId = "mb_id";
    private final String paramPwd = "mb_password";
    private Boolean isLogin = false;

    @Autowired
    private BoardDataProvider provider;

    @Override
    @PostConstruct
    public void login() {
        System.out.println("2cpu LOGIN => " + this.id);
        try {
            HttpResponse response = Request.Post(loginUrl)
                    .bodyForm(
                            new BasicNameValuePair(paramId, id),
                            new BasicNameValuePair(paramPwd, pwd)
                    )
                    .userAgent(UserAgentStr)
                    .execute().returnResponse();
            Util.log(response.getStatusLine() + " 2cpu login succeed.");
            this.isLogin = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Boolean authorized() {
        return this.isLogin;
    }

    @Override
    public Stream<BoardData> boardData() {
        try {
            String response = new String(Request.Get(this.bbsUrl)
                    .userAgent(UserAgentStr)
                    .execute().returnContent().asBytes(), "EUC_KR");

            return Jsoup.parse(response).select("#list_sell tbody tr").stream()
                    .filter(tr-> !(tr.hasClass("is_notice") || tr.hasClass("visible-xs")))
                    .map(tr -> tr.select("td"))
                    .map(tds -> this.provider.getInstance(BoardDataProvider.BoardType.TWOCPU, tds));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
