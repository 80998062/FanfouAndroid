package sinyuk.com.fanfou.api

/**
 * Created by sinyuk on 2018/4/24.
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│        _______. __  .__   __. ____    ____  __    __   __  ___   │
│       /       ||  | |  \ |  | \   \  /   / |  |  |  | |  |/  /   │
│      |   (----`|  | |   \|  |  \   \/   /  |  |  |  | |  '  /    │
│       \   \    |  | |  . `  |   \_    _/   |  |  |  | |    <     │
│   .----)   |   |  | |  |\   |     |  |     |  `--'  | |  .  \    │
│   |_______/    |__| |__| \__|     |__|      \______/  |__|\__\   │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
 */

/**
 * 显示指定用户及其好友的消息
 */
const val TIMELINE_HOME = "home_timeline"

/**
 * 显示指定用户及其好友的消息
 */
const val TIMELINE_FAVORITES = "favorites_timeline"

/**
 * 按照时间先后顺序显示消息上下文
 */
const val TIMELINE_CONTEXT = "context_timeline"

/**
 *
 */
const val TIMELINE_PHOTO = "photo_timeline"


const val TIMELINE_DRAFT = "draft_timeline"

/**
 * 浏览指定用户已发送消息
 */
const val TIMELINE_USER = "user_timeline"

/**
 * 回复当前用户的20条消息
 */
const val TIMELINE_REPLIES = "replies"

/**
 * 回复/提到当前用户的20条消息
 */
const val TIMELINE_MENTIONS = "mentions"


const val TIMELINE_PUBLIC = "public_timeline"

const val SEARCH_TIMELINE_PUBLIC = "search_public_timeline"

const val SEARCH_USERS = "search_users"

const val SEARCH_USER_TIMELINE = "search_user_timeline"

const val USERS_ADMIN = "admin"

const val USERS_FRIENDS = "friends"

const val USERS_FOLLOWERS = "followers"