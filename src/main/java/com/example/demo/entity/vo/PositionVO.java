package com.example.demo.entity.vo;

import com.example.demo.entity.Position;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PositionVO extends Position {
    private String companyName;
}
