package com.example.whatsfordinner.data.source.network.dao

import com.example.whatsfordinner.R

enum class TagType(val tagScreenTitle: Int) {
    INGREDIENT(R.string.ingredient_tag_screen_title),
    TYPE(R.string.type_tag_screen_title),
    HOLIDAY(R.string.holiday_tag_screen_title)
}