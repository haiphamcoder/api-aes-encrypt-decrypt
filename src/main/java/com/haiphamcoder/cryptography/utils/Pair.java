package com.haiphamcoder.cryptography.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair<T, K> {
    private T firstElement;
    private K secondElement;
}
