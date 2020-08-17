package machine
import java.util.*

fun main() {
    val input = Scanner(System.`in`)
    
    val coffeeMachine = Machine.create()
    while (coffeeMachine.status != Action.EXIT) {
        if (coffeeMachine.status == Action.PENDING) println("Write action (buy, fill, take, remaining, exit):")
        coffeeMachine.run(input.next())
    }
}

enum class Action {
    PENDING, BUY, BUY_CHOOSE, FILL, FILL_WATER, FILL_MILK, FILL_BEANS, FILL_CUPS, TAKE, REMAINING, EXIT;
}

enum class Coffee(val ingredients: MutableMap<String, Int>, val cost: Int) {
    ESPRESSO(mutableMapOf("water" to 250, "coffee beans" to 16, "disposable cups" to 1), 4),
    LATTE(mutableMapOf("water" to 350, "milk" to 75, "coffee beans" to 20, "disposable cups" to 1), 7),
    CAPPUCCINO(mutableMapOf("water" to 200, "milk" to 100, "coffee beans" to 12, "disposable cups" to 1), 6);
}

class Machine(val ingredients: MutableMap<String, Int>, var money: Int) {
    companion object {
        fun create() = Machine(mutableMapOf("water" to 400, "milk" to 540, "coffee beans" to 120, "disposable cups" to 9), 550)
    }

    var status = Action.PENDING
    fun run(action: String) {
        if (status == Action.PENDING) status = Action.valueOf(action.toUpperCase())
        when (status) {
            Action.BUY -> {status = Action.BUY; buy(action)}
            Action.FILL -> {status = Action.FILL; fill(action)}
            Action.TAKE -> take()
            Action.REMAINING -> info()
            Action.EXIT -> status = Action.EXIT
            else -> println("error")
        }
    }
    
    fun info() { println("""The coffee machine has:
    ${ingredients["water"]} of water
    ${ingredients["milk"]} of milk
    ${ingredients["coffee beans"]} of coffee beans
    ${ingredients["disposable cups"]} of disposable cups
    $$money of money""")
    status = Action.PENDING
    }
    
    fun buy(choose: String) {
        when (choose.toLowerCase()) {
            "buy" -> println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino:")
            "back" -> status = Action.PENDING
            "1" -> {makeCoffee(Coffee.ESPRESSO); status = Action.PENDING}
            "2" -> {makeCoffee(Coffee.LATTE); status = Action.PENDING}
            "3" -> {makeCoffee(Coffee.CAPPUCCINO); status = Action.PENDING}
            else -> println("error")
        }
    }
    
    fun makeCoffee(coffee: Coffee) {
        fun checkResurses(coffee: Coffee): Boolean {
            var result = true
            for (ing in coffee.ingredients.keys) {
                if (this.ingredients.getValue(ing) < coffee.ingredients.getValue(ing)) {
                    println("Sorry, not enough ${ing.toLowerCase()}!")
                    result = false
                }
            }
            return result
        }
        if (checkResurses(coffee)) {
            for (ing in coffee.ingredients.keys) {
                val result = this.ingredients.getValue(ing) - coffee.ingredients.getValue(ing)
                this.ingredients.set(ing, result)
            }
            this.money += coffee.cost
            println("I have enough resources, making you a coffee!")
        }
        status = Action.PENDING
    }
    
    var fillStatus = ""
    fun fill(value: String) {
        fun fillup() { 
            val result = this.ingredients.getValue(fillStatus) + value.toInt()
            ingredients.set(fillStatus, result)
        }
        when(fillStatus) {
            "" -> {fillStatus = "water"; println("Write how many ml of water do you want to add:")}
            "water" -> {fillup(); fillStatus = "milk"; println("Write how many ml of milk do you want to add:")}
            "milk" -> {fillup(); fillStatus = "coffee beans"; println("Write how many grams of coffee beans do you want to add:")}
            "coffee beans" -> {fillup(); fillStatus = "disposable cups"; println("Write how many disposable cups of coffee do you want to add:")}
            "disposable cups" -> { fillup(); status = Action.PENDING }
        }
    }
    
    fun take() {
        println("I gave you $$money")
        money = 0
        status = Action.PENDING
    }
}