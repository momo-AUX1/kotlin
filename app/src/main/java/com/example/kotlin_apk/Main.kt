fun main() {
    println("coucou");
    println(Lclasse);
    for (o in Lclasse){
        println(o.nom)
        println(o.surface())
    }
}

abstract class Piece() {
    open val largeur: Float = 0.0F;
    open val longueuer: Float = 0.0F;
    open val nom: String = "";

    open fun surface(): Float {
        return largeur * longueuer
    }
}


class Cuisine: Piece() {
    override val largeur: Float = 1.0F;
    override val longueuer: Float = 2.0F;
    override val nom: String = "Cuisine";
}

class Salon: Piece() {
    override val largeur: Float = 4.1F;
    override val longueuer: Float = 1.3F;
    override val nom: String = "Salon";
}

val X = Cuisine();
val Y = Salon();

val Lclasse: Array<Piece> = arrayOf(X, Y);