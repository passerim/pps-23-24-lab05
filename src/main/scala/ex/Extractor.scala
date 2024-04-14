package ex

import util.Optionals.Optional
import util.Sequences.Sequence

object SameTag:
  def unapply(items: Sequence[Item]): Option[String] =
    items
      .flatMap(_.tags)
      .filter(t => items.map(_.tags.contains(t)).find(_ == false).isEmpty)
      .head match
      case Optional.Just(t) => Some(t)
      case _                => None

@main def tryExtractor(): Unit =
  val item1 = Item(0, "Item0", "A", "B")
  val item2 = Item(1, "Item1", "A")
  val item3 = Item(2, "Item2", "A", "C")
  val items = Sequence(item1, item2, item3)
  assert("A" == SameTag.unapply(items).getOrElse(""))
  items match
    case SameTag(t) => println(s"$items have same tag $t")
    case _          => println(s"$items have different tags")
