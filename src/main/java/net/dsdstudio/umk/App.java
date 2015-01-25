package net.dsdstudio.umk;

import net.dsdstudio.umk.services.ClienBoardDataGrabService;
import net.dsdstudio.umk.services.SlrBoardDataGrabService;
import net.dsdstudio.umk.utils.Util;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * usedmarket-collector
 * net.dsdstudio.umk
 *
 * @author : bhkim
 * @since : 2015. 1. 9..
 */

public class App {


    public static void main(String[] args) {
                        /*
        SlrBoardDataGrabService slrGrabber = new SlrBoardDataGrabService("your_id", "your_pwd");
        ClienBoardDataGrabService clienGrabber = new ClienBoardDataGrabService("your_id", "your_pwd");
        clienGrabber.login();
        slrGrabber.login();

        service.scheduleAtFixedRate(() -> {
            List<BoardData> clienList = clienGrabber.boardData().collect(Collectors.toList());
            List<BoardData> slrList = slrGrabber.boardData().collect(Collectors.toList());

            OptionalInt slrMaxBoardId = slrList.stream().mapToInt(o -> o.id).reduce(Integer::max);
            OptionalInt clienMaxBoardId = clienList.stream().mapToInt(o -> o.id).reduce(Integer::max);
            Util.log("clien max boardId : " + clienMaxBoardId.getAsInt());
            Util.log("slrclub max boardId : " + slrMaxBoardId.getAsInt());
            Util.log(clienList + " " + slrList);
            Util.log(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss.SSS")) + " executed");
        }, 0, periodSeconds, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Util.log("Shutdown Hook started..");
                service.shutdownNow();
                try {
                    service.awaitTermination(0, TimeUnit.MILLISECONDS);
                    Util.log("Shutdown completed..");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        */
        SpringApplication.run(MainController.class, args);
    }
}
