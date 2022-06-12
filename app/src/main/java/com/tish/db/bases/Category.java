package com.tish.db.bases;

import com.tish.MainActivity;
import com.tish.R;

public enum Category {
    FOOD("Харчування", R.drawable.category_food_icon, R.color.food),
    HEALTH("Здоров'я", R.drawable.category_health_icon, R.color.health),
    HOME("Дім", R.drawable.category_home_icon, R.color.home),
    HYGIENE("Гігієна", R.drawable.category_hygiene_icon, R.color.hygiene),
    CLOTHES("Одяг", R.drawable.category_clothes_icon, R.color.clothes),
    HOUSECOSTS("Комунальні послуги", R.drawable.category_housecosts_icon, R.color.housecosts),
    TRANSPORT("Громадський транспорт", R.drawable.category_transport_icon, R.color.transport),
    CAR("Персональний транспорт", R.drawable.category_car_icon, R.color.car),
    CAFFEE("Кафе та ресторани", R.drawable.category_caffee_icon, R.color.caffee),
    CINEMA("Розваги", R.drawable.category_cinema_icon, R.color.cinema),
    TRAVEL("Подорожі", R.drawable.category_travel_icon, R.color.travel),
    EDUCATION("Освіта", R.drawable.category_education_icon, R.color.education),
    HOBBY("Захоплення", R.drawable.category_hobby_icon, R.color.hobby),
    GIFTS("Подарунки", R.drawable.category_gifts_icon, R.color.gifts),
    CHILDREN("Діти", R.drawable.category_children_icon, R.color.children),
    PETS("Домашні улюбленці", R.drawable.category_pets_icon, R.color.pets),
    MAKEUP("Догляд за собою", R.drawable.category_makeup_icon, R.color.makeup),
    OTHER("Інше", R.drawable.category_other_icon, R.color.other);

    private String categoryName;
    private int iconResource;
    private int colorResource;

    Category(String categoryName, int iconResource, int colorResource) {
        this.categoryName = categoryName;
        this.iconResource = iconResource;
        this.colorResource = colorResource;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getIconResource() {
        return iconResource;
    }

    public int getColorResource() {
        return colorResource;
    }
}
