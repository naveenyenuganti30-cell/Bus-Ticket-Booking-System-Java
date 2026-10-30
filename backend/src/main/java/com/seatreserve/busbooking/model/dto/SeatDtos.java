package com.seatreserve.busbooking.model.dto;

import java.util.List;

public class SeatDtos {

    public static class SeatDto {
        private Long journeySeatId;
        private Long seatId;
        private String seatNumber;
        private String deck;
        private String seatType;
        private String position;
        private Integer seatRow;
        private Integer seatCol;
        private String status;
        private Double price;
        private Double surchargePercentage;

        public SeatDto() {}

        public Long getJourneySeatId() { return journeySeatId; }
        public void setJourneySeatId(Long journeySeatId) { this.journeySeatId = journeySeatId; }
        public Long getSeatId() { return seatId; }
        public void setSeatId(Long seatId) { this.seatId = seatId; }
        public String getSeatNumber() { return seatNumber; }
        public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
        public String getDeck() { return deck; }
        public void setDeck(String deck) { this.deck = deck; }
        public String getSeatType() { return seatType; }
        public void setSeatType(String seatType) { this.seatType = seatType; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public Integer getSeatRow() { return seatRow; }
        public void setSeatRow(Integer seatRow) { this.seatRow = seatRow; }
        public Integer getSeatCol() { return seatCol; }
        public void setSeatCol(Integer seatCol) { this.seatCol = seatCol; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public Double getSurchargePercentage() { return surchargePercentage; }
        public void setSurchargePercentage(Double surchargePercentage) { this.surchargePercentage = surchargePercentage; }
    }

    public static class SeatLayoutResponse {
        private Long journeyId;
        private String busName;
        private String busType;
        private String busTypeDisplay;
        private String layoutType;
        private long totalSeats;
        private long availableSeats;
        private long bookedSeats;
        private Double baseFare;
        private List<SeatDto> lowerDeck;
        private List<SeatDto> upperDeck;

        public SeatLayoutResponse() {}

        public Long getJourneyId() { return journeyId; }
        public void setJourneyId(Long journeyId) { this.journeyId = journeyId; }
        public String getBusName() { return busName; }
        public void setBusName(String busName) { this.busName = busName; }
        public String getBusType() { return busType; }
        public void setBusType(String busType) { this.busType = busType; }
        public String getBusTypeDisplay() { return busTypeDisplay; }
        public void setBusTypeDisplay(String busTypeDisplay) { this.busTypeDisplay = busTypeDisplay; }
        public String getLayoutType() { return layoutType; }
        public void setLayoutType(String layoutType) { this.layoutType = layoutType; }
        public long getTotalSeats() { return totalSeats; }
        public void setTotalSeats(long totalSeats) { this.totalSeats = totalSeats; }
        public long getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(long availableSeats) { this.availableSeats = availableSeats; }
        public long getBookedSeats() { return bookedSeats; }
        public void setBookedSeats(long bookedSeats) { this.bookedSeats = bookedSeats; }
        public Double getBaseFare() { return baseFare; }
        public void setBaseFare(Double baseFare) { this.baseFare = baseFare; }
        public List<SeatDto> getLowerDeck() { return lowerDeck; }
        public void setLowerDeck(List<SeatDto> lowerDeck) { this.lowerDeck = lowerDeck; }
        public List<SeatDto> getUpperDeck() { return upperDeck; }
        public void setUpperDeck(List<SeatDto> upperDeck) { this.upperDeck = upperDeck; }
    }
}
