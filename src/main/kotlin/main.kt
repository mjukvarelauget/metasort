import kotlin.random.Random

const val MAX_NUM = 10000
const val LENGTH = 1000000
val RANDOM = Random(System. currentTimeMillis())

class Table {
    private val array: Array<Int>
    private val offset: Int
    val length: Int
    var silent: Boolean = false

    constructor(length: Int) {
        array = Array(length) {
            RANDOM.nextInt(MAX_NUM)
        }
        offset = 0
        this.length = length
    }

    constructor(table: Table, offset: Int, length: Int) {
        array = table.array
        assert(offset >= 0 && length >= 0 && offset+length <= table.length)
        this.offset = table.offset + offset
        this.length = length
    }

    operator fun get(index: Int):Int {
        assert(index in 0 until length)
        if(!silent)
            getCount[currentSort]++
        return array[offset+index]
    }

    operator fun set(index: Int, value:Int ) {
        assert(index in 0 until length)
        if(!silent)
            setCount[currentSort]++
        array[offset+index] = value
    }

    fun swap(a: Int, b: Int) {
        assert(a in 0 until length)
        assert(b in 0 until length)
        if(!silent)
            swapCount[currentSort]++
        array[offset+a] = array[offset+b].also {array[offset+b] = array[offset+a]}
    }

    fun dump() {
        for (i in array)
            print("$i, ")
        println()
    }
}

fun mergesort(table: Table) {
    if(table.length <= 1)
        return

    val mid = table.length / 2

    // Sorter øvre og nedre halvdel for seg
    val lo = Table(table, 0, mid)
    val hi = Table(table, mid, table.length-mid)
    sort(lo)
    sort(hi)

    merge(table, mid)
}

// mid is the first index of the high interval
fun merge(table: Table, mid: Int) {
    // targets[0] sier hvor i tabellen det minste ellementet ligger, osv.
    val targets = Array(table.length) {0}
    var lo = 0
    var hi = 0
    while(lo < mid && mid+hi < table.length) {
        if(table[lo] < table[mid+hi]) {
            targets[lo+hi] = lo
            lo++
        } else {
            targets[lo+hi] = mid+hi
            hi++
        }
    }
    while(lo < mid) {
        targets[lo+hi] = lo
        lo++
    }
    while(mid+hi < table.length) {
        targets[lo+hi] = mid+hi
        hi++
    }

    // targetsInverse[i] sier hvor i den sorterte tabellen tallet på indeks i skal ligge
    val targetsInverse = Array(table.length) {0}
    targets.forEachIndexed { i, value -> targetsInverse[value] = i }

    for(i in 0 until table.length) {
        // targets[i] is the (index of the) one supposed to be here
        val j = targets[i]
        if(j == i)
            continue

        // Swap what what is in i with what we want in i
        table.swap(i, j)

        // The index j now has what used to be in i

        // It still belongs to the same place in the end
        targetsInverse[j] = targetsInverse[i]
        // That place now knows what index its future value is at
        targets[targetsInverse[j]] = j

        // The index i now has the ith smallest element
        // The ith smallest element is at index i
        targets[i] = i
        targetsInverse[i] = i
    }
}

fun quicksort(table: Table) {
    if(table.length <= 1)
        return
    val pivot = table[RANDOM.nextInt(table.length)]
    val mid = partition(table, pivot)
    sort(Table(table, 0, mid))
    sort(Table(table, mid, table.length-mid))
}

fun partition(table: Table, pivot: Int): Int {
    var lo = 0
    var hi = table.length-1

    while(true) {
        while(lo <= hi && table[lo] < pivot)
            lo++
        while(lo <= hi && table[hi] > pivot)
            hi--

        if (lo > hi)
            return lo

        table.swap(lo, hi)
        lo++
        hi--
    }
}

fun insert(table: Table, index: Int) {
    var insert = index
    while(insert > 0 && table[insert-1] > table[insert]) {
        table.swap(insert-1, insert)
        insert--
    }
}

fun insertionsort(table: Table) {
    if(table.length <= 1)
        return
    val mid = RANDOM.nextInt(table.length)

    // Sort everything below mid
    sort(Table(table, 0, mid))

    // Move mid into correct place in sorted
    insert(table, mid)

    // Sort everything above the sorted part
    sort(Table(table, mid+1, table.length-mid-1))

    // Merge the two intervals
    merge(table, mid+1)
}

val sorts = arrayOf(::quicksort, ::insertionsort, ::mergesort)
val names = arrayOf("Quicksort", "Insertionsort", "Mergesort")
var currentSort = 0
val callCount = Array(sorts.size) {0}
val getCount = Array(sorts.size) {0}
val setCount = Array(sorts.size) {0}
val swapCount = Array(sorts.size) {0}

fun sort(table: Table) {
    val oldSort = currentSort
    currentSort = RANDOM.nextInt(sorts.size)
    callCount[currentSort]++
    sorts[currentSort](table)
    currentSort = oldSort

}

fun checkSorted(table: Table) {
    for(inx in 1 until table.length)
        if(table[inx-1] > table[inx])
            return println("Sorting failed!")
}

fun main() {
    println("Velkommen til mjuksort")

    val table = Table(LENGTH)
    sort(table)
    table.silent = true
    // table.dump()
    checkSorted(table)

    for(i in sorts.indices) {
        println(names[i])
        println("Called: ${callCount[i]}")
        println("Getters: ${getCount[i]}")
        println("Setters: ${setCount[i]}")
        println("Swaps: ${swapCount[i]}")
        println()
    }
}