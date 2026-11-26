package com.senla.binancepriceservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table(name="symbol_prices")
@Entity
public class PriceSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length=20)
    private String symbol;
    @Column(nullable=false, precision = 20, scale = 8)
    private BigDecimal price;
    @Column(nullable = false)
    private Long binanceCloseTime;
    @Column(nullable = false)
    private Instant fetchedAt;

    protected PriceSnapshot(){

    }
    public PriceSnapshot(String symbol, BigDecimal price, Long binanceCloseTime, Instant fetchedAt){
        this.symbol = symbol;
        this.price = price;
        this.binanceCloseTime = binanceCloseTime;
        this.fetchedAt = fetchedAt;
    }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getBinanceCloseTime() {
        return binanceCloseTime;
    }

    public Instant getFetchedAt() {
        return fetchedAt;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setBinanceCloseTime(Long binanceCloseTime) {
        this.binanceCloseTime = binanceCloseTime;
    }

    public void setFetchedAt( Instant fetchedAt) {
        this.fetchedAt = fetchedAt;
    }
}
