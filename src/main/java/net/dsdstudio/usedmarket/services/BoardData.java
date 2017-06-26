package net.dsdstudio.usedmarket.services;

import lombok.Builder;
import lombok.Data;

/**
 * BoardData Model
 */
@Data
@Builder
public class BoardData {
    private BoardDataProvider.BoardType dataType;
    private Integer id;
    private String subject;
    private String detailUrl;
    private String ownerName;
    private String date;

    public String getMessage() {
        return dataType + " [" + date + "] " + subject + "\n" + getDetailUrl();
    }
}
