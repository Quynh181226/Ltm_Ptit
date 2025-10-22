package model;

import java.io.Serializable;

public class Asset implements Serializable {

     private static final long serialVersionUID = 1L;

      private String id;

     private String name;
   private String type;
  private String roomId;

    private double value;

    public Asset() {}

      public Asset(String id, String name, String type, String roomId, double value) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.roomId = roomId;
        this.value = value;
    }
   public String getId() {
        return id;
    }
  public String getName() {
        return name;
    }

   public String getType() {
        return type;
    }

     public String getRoomId() {
        return roomId;
    }
 public double getValue() {
        return value;
    }

}