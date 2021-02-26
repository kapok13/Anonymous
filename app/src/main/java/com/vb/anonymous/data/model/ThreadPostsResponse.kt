package com.vb.anonymous.data.model

import com.google.gson.annotations.SerializedName

data class ThreadPostsResponse (

    val board : String,
    val boardInfo : String,
    val boardInfoOuter : String,
    val boardName : String,

    @SerializedName("advert_bottom_image") val advertBottomImage : String,
    val advert_bottom_link : String,
    val advert_mobile_image : String,
    val advert_mobile_link : String,
    val advert_top_image : String,
    val advert_top_link : String,
    val board_banner_image : String,
    val board_banner_link : String,
    val bump_limit : Int,
    val current_thread : Int,
    val default_name : String,
    val enable_dices : Int,
    val enable_flags : Int,
    val enable_icons : Int,
    val enable_images : Int,
    val enable_likes : Int,
    val enable_names : Int,
    val enable_oekaki : Int,
    val enable_posting : Int,
    val enable_sage : Int,
    val enable_shield : Int,
    val enable_subject : Int,
    val enable_thread_tags : Int,
    val enable_trips : Int,
    val enable_video : Int,
    val files_count : Int,
    val is_board : Int,
    val is_closed : Int,
    val is_index : Int,
    val max_comment : Int,
    val max_files_size : Int,
    val max_num : Int,
    val news_abu : List<NewsAbu>,
    val posts_count : Int,
    val thread_first_image : String,
    val threads : List<Threads>,
    val title : String,
    val top : List<Top>,
    val unique_posters : Int
)