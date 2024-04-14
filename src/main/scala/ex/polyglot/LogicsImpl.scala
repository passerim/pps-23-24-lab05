package ex.polyglot

import ex.polyglot.Cell.*
import ex.polyglot.UtilExtensions.*
import util.Optionals.Optional
import util.Sequences.Sequence
import util.Streams.*

import scala.annotation.tailrec
import scala.util.Random

object UtilExtensions:
  import util.Sequences.Sequence.*

  extension [A](seq: Sequence[A])
    def isEmpty: Boolean = seq match
      case Nil() => true
      case _     => false

    def size: Integer = seq match
      case Cons(_, t) => 1 + t.size
      case _          => 0

case class Position(row: Int, col: Int)

enum Cell(val position: Position):
  case Empty(override val position: Position, flag: Boolean = false) extends Cell(position)
  case Mine(override val position: Position, flag: Boolean = false)  extends Cell(position)
  case Swept(override val position: Position)                        extends Cell(position)

trait Grid:
  def cells: Sequence[Cell]
  def getCell(position: Position): Optional[Cell]
  def getNeighboursOfACell(cell: Cell): Sequence[Cell]
  def setCell(position: Position, newCell: Cell): Unit

object Grid:
  def apply(size: Int, mines: Int): Grid = GridImpl(size, mines)

  private class GridImpl(val size: Int, val mines: Int) extends Grid:
    private val NEIGHBOR_DISTANCE = 1
    private val random            = Random()
    private var _cells: Sequence[Cell] = Stream
      .iterate(0)(_ + 1)
      .take(size)
      .flatMap(i => Stream.iterate(0)(_ + 1).take(size).map(j => Empty(Position(i, j))))
      .toList
    for i <- 1 to mines
    do placeRandomMine()

    @tailrec
    private def placeRandomMine(): Unit = {
      val position = Position(random.nextInt(size), random.nextInt(size))
      if _cells.filter(c => c.isInstanceOf[Mine] && c.position == position).isEmpty
      then setCell(position, Mine(position))
      else placeRandomMine()
    }

    private def isNeighbour(cell1: Cell, cell2: Cell): Boolean =
      val distanceX = cell1.position.row - cell2.position.row
      val distanceY = cell1.position.col - cell2.position.col
      Math.max(Math.abs(distanceX), Math.abs(distanceY)) == NEIGHBOR_DISTANCE

    def cells: Sequence[Cell] =
      _cells

    def getCell(position: Position): Optional[Cell] =
      _cells.find(c => c.position == position)

    def setCell(position: Position, newCell: Cell): Unit =
      _cells = _cells.map({
        case c if c.position == position => newCell
        case c                           => c
      })

    def getNeighboursOfACell(cell: Cell): Sequence[Cell] =
      _cells.filter(isNeighbour(cell, _))

class LogicsImpl(val size: Int, val mines: Int) extends Logics:
  private val grid: Grid = Grid(size, mines)

  private def getPosition(row: Int, col: Int): Position =
    if row < 0 || col < 0 || row >= size || col >= size then throw new IndexOutOfBoundsException
    else Position(row, col)

  override def aMineWasFound(row: Int, col: Int): Boolean =
    val position = getPosition(row, col)
    grid
      .getCell(position)
      .map({
        case Mine(_, _) => true
        case Swept(_)   => false
        case cell =>
          grid.setCell(cell.position, Swept(cell.position))
          getSweptCellCounter(cell.position.row, cell.position.col).map(count =>
            if count == 0 then
              grid
                .getNeighboursOfACell(cell)
                .map({
                  case Empty(position, _) => aMineWasFound(position.row, position.col)
                  case _                  => ()
                })
          )
          false
      })
      .orElse(false)

  override def getSweptCellCounter(row: Int, col: Int): java.util.Optional[Integer] =
    val position = getPosition(row, col)
    grid
      .getCell(position)
      .filter({
        case Swept(_) => true
        case _        => false
      })
      .map(
        grid
          .getNeighboursOfACell(_)
          .filter({
            case Mine(_, _) => true
            case _          => false
          })
          .size
      ) match
      case Optional.Just(c) => java.util.Optional.of(c)
      case _                => java.util.Optional.empty()

  override def isThereFlag(row: Int, col: Int): Boolean =
    val position = getPosition(row, col)
    grid.getCell(position) match
      case Optional.Just(Empty(_, f)) => f
      case Optional.Just(Mine(_, f))  => f
      case _                          => false

  override def isThereMine(row: Int, col: Int): Boolean =
    val position = getPosition(row, col)
    grid.getCell(position) match
      case Optional.Just(Mine(_, _)) => true
      case _                         => false

  override def isThereVictory: Boolean =
    grid.cells.filter(_.isInstanceOf[Cell.Empty]).isEmpty

  override def toggleFlag(row: Int, col: Int): Unit =
    val position = getPosition(row, col)
    grid.getCell(position) match
      case Optional.Just(Empty(p, f)) => grid.setCell(position, Empty(p, !f))
      case Optional.Just(Mine(p, f))  => grid.setCell(position, Mine(p, !f))
      case _                          => ()
