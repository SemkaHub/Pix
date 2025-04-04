package com.example.pix.domain.model

enum class PictureSize(
    val prefix: String,
    val width: Int
) {
    S("s", 75),
    Q("q", 100),
    M("m", 240),
    N("n", 320),
    W("w", 400),
    Z("z", 640),
    C("c", 800),
    B("b", 1024),
    H("h", 1600),
    K("k", 2048),
    K3("3k", 3072),
    K4("4k", 4096)
}