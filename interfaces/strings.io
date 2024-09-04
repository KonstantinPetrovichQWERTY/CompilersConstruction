class Character extends AnyRef is
    method chr(): Integer is end
    this(input: Integer) is end
    this(input: Character) is end
end

class String extends Character, Array[Character] is
    var data: Array[Character]
    this(input: Array[Character]) is end
    method Length: Integer is end
    method Plus(other: String): String is end
end
