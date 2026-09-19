package com.goodpang.util;

import java.util.Set;

public class PaymentMethodValidator {

    private static final Set<String> BANK_CODES = Set.of(
            "SHINHAN", "KB", "WOORI", "NH", "HANA", "KAKAO", "TOSS"
    );

    private static final Set<String> CARD_COMPANIES = Set.of(
            "SHINHAN", "KB", "SAMSUNG", "HYUNDAI", "LOTTE", "HANA"
    );

    private PaymentMethodValidator() {
    }

    public static String validatePaymentType(String paymentType) {
        if (paymentType == null || paymentType.isBlank()) {
            return "결제수단 종류가 없습니다.";
        }

        if (!"BANK".equals(paymentType) && !"CARD".equals(paymentType)) {
            return "지원하지 않는 결제수단입니다.";
        }

        return null;
    }

    public static String validateBank(
            String bankCode,
            String accountNumber,
            String accountHolder) {

        if (bankCode == null || !BANK_CODES.contains(bankCode)) {
            return "올바른 은행을 선택해주세요.";
        }

        if (accountNumber == null || accountNumber.isBlank()) {
            return "계좌번호를 입력해주세요.";
        }

        String number = accountNumber.replaceAll("[^0-9]", "");

        if (!number.matches("\\d{8,16}")) {
            return "계좌번호는 숫자 8~16자리로 입력해주세요.";
        }

        if (accountHolder == null || accountHolder.isBlank()) {
            return "예금주를 입력해주세요.";
        }

        String holder = accountHolder.trim();

        if (holder.length() < 2 || holder.length() > 30) {
            return "예금주는 2~30자로 입력해주세요.";
        }

        if (!holder.matches("^[가-힣a-zA-Z\\s]+$")) {
            return "예금주에는 한글 또는 영문만 사용할 수 있습니다.";
        }

        return null;
    }

    public static String validateCard(
            String cardCompany,
            String cardNumber) {

        if (cardCompany == null || !CARD_COMPANIES.contains(cardCompany)) {
            return "올바른 카드사를 선택해주세요.";
        }

        if (cardNumber == null || cardNumber.isBlank()) {
            return "카드번호를 입력해주세요.";
        }

        String number = cardNumber.replaceAll("[^0-9]", "");

        if (!number.matches("\\d{13,19}")) {
            return "카드번호 형식이 올바르지 않습니다.";
        }

        return null;
    }
}