package com.discut.manga.data

class SnowFlakeUtil(private val datacenterId: Long, private val machineId: Long) {

    private var sequence = 0L // 序列号
    private var lastStmp = -1L // 上次的时间戳

    init {
        require(!(datacenterId > MAX_DATACENTER_NUM || datacenterId < 0)) { "datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0" }
        require(!(machineId > MAX_MACHINE_NUM || machineId < 0)) { "machineId can't be greater than MAX_MACHINE_NUM or less than 0" }
    }

    @get:Synchronized
    val nextId: Long
        // 产生下一个ID
        get() {
            var currStmp = newstmp
            if (currStmp < lastStmp) {
                throw RuntimeException("Clock moved backwards.Refusing to generate id")
            }
            if (currStmp == lastStmp) {
                // 若在相同毫秒内 序列号自增
                sequence = sequence + 1 and MAX_SEQUENCE
                // 同一毫秒的序列数已达到最大
                if (sequence == 0L) {
                    currStmp = nextMill
                }
            } else {
                // 若在不同毫秒内 则序列号置为0
                sequence = 0L
            }
            lastStmp = currStmp
            return (currStmp - START_STMP) shl TIMESTMP_LEFT or datacenterId shl DATACENTER_LEFT or machineId shl MACHINE_LEFT or sequence // 序列号部分
        }
    private val nextMill: Long
        // 获取新的毫秒数
        get() {
            var mill = newstmp
            while (mill <= lastStmp) {
                mill = newstmp
            }
            return mill
        }
    private val newstmp: Long
        // 获取当前的毫秒数
        get() = System.currentTimeMillis()

    companion object {
        // 起始时间戳
        private const val START_STMP = 1480166465631L

        // 每部分的位数
        private const val SEQUENCE_BIT: Int = 12 // 序列号占用位数
        private const val MACHINE_BIT: Int = 5 // 机器id占用位数
        private const val DATACENTER_BIT: Int = 5 // 机房id占用位数

        // 每部分最大值
        private const val MAX_DATACENTER_NUM = -1L xor (-1L shl DATACENTER_BIT)
        private const val MAX_MACHINE_NUM = -1L xor (-1L shl MACHINE_BIT)
        private const val MAX_SEQUENCE = -1L xor (-1L shl SEQUENCE_BIT)

        // 每部分向左的位移
        private const val MACHINE_LEFT = SEQUENCE_BIT
        private const val DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT
        private const val TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT

        private val snowFlake = SnowFlakeUtil(datacenterId = 20L, machineId = 24L)

        fun generateSnowFlake(): Long {
            return snowFlake.nextId
        }
    }
}