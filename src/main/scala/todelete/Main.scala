package todelete

case class Transaction(userId: Int, amount: Double)




object Main {

  def main(args: Array[String]): Unit = {
    val transactions = List(
      Transaction(1, 100.0),
      Transaction(2, 50.0),
      Transaction(1, 75.0),
      Transaction(3, 200.0),
      Transaction(2, 25.0)
    )
    
    val testing:Option[Int] = Some(1)

    val maybeInt = testing.flatMap(Some(_))
    val maybeInt2 = testing.map(_ * 2)
  }
  def totalByUser(transactions: List[Transaction]): Map[Int, Double] =
    transactions.groupMapReduce(_._1)(_._2)(_+_)
    
    
  
}
