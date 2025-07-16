package com.example.virtualthreadservice.dto;

import java.util.Map;

/**
 * RequestDTO, /submit endpoint’inden alınan JSON verisini temsil eder.
 * payload alanı, dinamik yapıdaki herhangi bir JSON içeriğini Map olarak taşır.
 * Dış istemciden gelen ham veriyi servis katmanına taşımak için kullanılır.
 */
public record RequestDTO(Map<String, Object> payload) {
}
