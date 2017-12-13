package kotlin.de.roamingthings.wsepd

val lutVcomDc = intArrayOf(
        0x00, 0x00,
        0x00, 0x0F, 0x0F, 0x00, 0x00, 0x05,
        0x00, 0x32, 0x32, 0x00, 0x00, 0x02,
        0x00, 0x0F, 0x0F, 0x00, 0x00, 0x05,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00
)

//R21H
val lutWw = intArrayOf(
        0x50, 0x0F, 0x0F, 0x00, 0x00, 0x05,
        0x60, 0x32, 0x32, 0x00, 0x00, 0x02,
        0xA0, 0x0F, 0x0F, 0x00, 0x00, 0x05,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00
)

//R22H    r
val lutBw = intArrayOf(
        0x50, 0x0F, 0x0F, 0x00, 0x00, 0x05,
        0x60, 0x32, 0x32, 0x00, 0x00, 0x02,
        0xA0, 0x0F, 0x0F, 0x00, 0x00, 0x05,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00
)

//R24H    b
val lutBb = intArrayOf(
        0xA0, 0x0F, 0x0F, 0x00, 0x00, 0x05,
        0x60, 0x32, 0x32, 0x00, 0x00, 0x02,
        0x50, 0x0F, 0x0F, 0x00, 0x00, 0x05,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00
)

//R23H    w
val lutWb = intArrayOf(
        0xA0, 0x0F, 0x0F, 0x00, 0x00, 0x05,
        0x60, 0x32, 0x32, 0x00, 0x00, 0x02,
        0x50, 0x0F, 0x0F, 0x00, 0x00, 0x05,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00
)