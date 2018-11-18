package slawomir.kustra.kodeinexample.data.model

data class Quote(val text: String, val author: String) {

    override fun toString() = "$text - $author"

}