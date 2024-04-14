package ex

import scala.annotation.targetName

// Express a second degree polynomial
// Structure: secondDegree * X^2 + firstDegree * X + constant
trait SecondDegreePolynomial:
  def constant: Double
  def firstDegree: Double
  def secondDegree: Double
  @targetName("sum") def +(polynomial: SecondDegreePolynomial): SecondDegreePolynomial
  @targetName("minus") def -(polynomial: SecondDegreePolynomial): SecondDegreePolynomial

object SecondDegreePolynomial:
  def apply(secondDegree: Double, firstDegree: Double, constant: Double): SecondDegreePolynomial =
    SecondDegreePolynomialImpl(secondDegree, firstDegree, constant)

  private case class SecondDegreePolynomialImpl(
      secondDegree: Double,
      firstDegree: Double,
      constant: Double
  ) extends SecondDegreePolynomial:
    @targetName("sum")
    override def +(polynomial: SecondDegreePolynomial): SecondDegreePolynomial =
      SecondDegreePolynomialImpl(
        secondDegree + polynomial.secondDegree,
        firstDegree + polynomial.firstDegree,
        constant + polynomial.constant
      )

    @targetName("minus")
    override def -(polynomial: SecondDegreePolynomial): SecondDegreePolynomial =
      SecondDegreePolynomialImpl(
        secondDegree - polynomial.secondDegree,
        firstDegree - polynomial.firstDegree,
        constant - polynomial.constant
      )

@main def checkComplex(): Unit =
  val simplePolynomial  = SecondDegreePolynomial(1.0, 0, 3)
  val anotherPolynomial = SecondDegreePolynomial(0.0, 1, 0.0)
  val fullPolynomial    = SecondDegreePolynomial(3.0, 2.0, 5.0)
  val sum               = simplePolynomial + anotherPolynomial
  assert(SecondDegreePolynomial(1, 1, 3) == sum)
  println:
    (sum, sum.secondDegree, sum.firstDegree, sum.constant) // 1.0 * X^2 + 1.0 * X + 3.0
  val multipleOperations = fullPolynomial - (anotherPolynomial + simplePolynomial)
  assert(SecondDegreePolynomial(2, 1, 2) == multipleOperations)
  println:
    (
      multipleOperations,
      multipleOperations.secondDegree,
      multipleOperations.firstDegree,
      multipleOperations.constant
    ) // 2.0 * X^2 + 1.0 * X + 2.0
