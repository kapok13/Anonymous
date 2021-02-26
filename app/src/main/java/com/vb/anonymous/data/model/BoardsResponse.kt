package com.vb.anonymous.data.model

data class BoardsResponse (

	val boards : List<Boards>,
	val global_boards : Int,
	val global_posts : String,
	val global_speed : String,
	val is_index : Int,
	val tags : List<Tags>,
	val type : Int
)