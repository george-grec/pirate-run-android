package com.example.piraterun.data.entities

data class User(
    var id: String,
    var username: String,
    var email: String,
    var password: String,
    var firstName: String,
    var lastName: String,
    var ranks: ArrayList<String>,
    var selectedRank: String,
    var avatars: ArrayList<String>,
    var selectedAvatar: String,
    var coins: Int
) {
    constructor() : this("", "", "", "", "", "",
        ArrayList<String>(), "", ArrayList<String>(), "ic_cook", 0)
}