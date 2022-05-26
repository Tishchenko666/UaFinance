package com.tish.db.bases;

public enum Season {
    WINTER("Зима", new int[]{1, 2, 12}),
    SPRING("Весна", new int[]{3, 4, 5}),
    SUMMER("Літо", new int[]{6, 7, 8}),
    AUTUMN("Осінь", new int[]{9, 10, 11});

    String name;
    int[] numbers;

    Season(String name, int[] numbers) {
        this.name = name;
        this.numbers = numbers;
    }

    public String getName() {
        return name;
    }

    public int[] getNumbers() {
        return numbers;
    }
}
