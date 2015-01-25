package net.dsdstudio.umk;

import net.dsdstudio.umk.services.BoardDataProvider;

/**
 * BoardData Model
 */
public class BoardData {
    public BoardDataProvider.BoardType dataType;
    public Integer id;
    public String subject;
    public String detailUrl;
    public String ownerName;
    public String date;

    @Override
    public String toString() {
        return "BoardData{" +
                "dataType=" + dataType +
                ", id=" + id +
                ", subject='" + subject + '\'' +
                ", detailUrl='" + detailUrl + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
