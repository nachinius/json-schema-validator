package contrib

// https://github.com/busymachines/newtypes-tapir-example
object codecs extends NewtypesCodecs {

  import io.circe.Codec
  import io.circe.generic.codec.DerivedAsObjectCodec
  import shapeless.Lazy

  type Codec[A] = io.circe.Codec[A]

  def deriveCodec[A](implicit codec: Lazy[DerivedAsObjectCodec[A]]): Codec.AsObject[A] = codec.value
}
