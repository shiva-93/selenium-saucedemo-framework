package com.framework.models;

/**
 * Immutable checkout form data for SauceDemo step one.
 * <p>
 * WHY Builder: multi-field form data without telescoping constructors;
 * callers can set only what a scenario needs and {@link #build()} freezes values
 * so page objects cannot mutate shared test data mid-flow.
 */
public final class CheckoutInfo {

    private final String firstName;
    private final String lastName;
    private final String postalCode;

    private CheckoutInfo(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.postalCode = builder.postalCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + postalCode + ")";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String firstName;
        private String lastName;
        private String postalCode;

        private Builder() {
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public CheckoutInfo build() {
            if (firstName == null || lastName == null || postalCode == null) {
                throw new IllegalStateException(
                        "CheckoutInfo requires firstName, lastName, and postalCode");
            }
            return new CheckoutInfo(this);
        }
    }
}
