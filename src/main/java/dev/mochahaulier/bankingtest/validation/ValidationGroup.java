package dev.mochahaulier.bankingtest.validation;

import jakarta.validation.groups.Default;

public interface ValidationGroup {
    interface NewOperation extends Default {
    }

    interface UpdateOperation extends Default {
    }
}
