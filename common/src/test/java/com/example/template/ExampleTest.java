package com.example.template;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ExampleTest {
    @Test
    void placeholderTest() {
        assertThat(TemplateMod.MOD_ID).isEqualTo("template");
    }
}
