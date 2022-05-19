use io
use conv

class Point{ // a mutable point
    x: int;
    y: int;

    move(dx: int, dy: int) {
        x = x + dx
        y = y + dy
    }
    add(p: Point): Point {
        return createPoint(x + p.x, y + p.y)
    }
    initPoint(x0: int, y0: int): Point {
        x = x0
        y = y0
        return this
    }
    clone(): Point { return createPoint(x, y) }
}

createPoint(x: int, y:int): Point {
    return new Point.initPoint(x, y)
}

class Color {
    r, g, b: int
}

class ColoredPoint extends Point {
    col: Color
    color(): Color { return col }

    initColoredPoint(x0: int, y0: int, c: Color): ColoredPoint {
        col = c
        _ = initPoint(x0, y0)
        return this
    }
}

main(args:int[][]) {
    c:Color = new Color
    c.r = 1; c.g = 2; c.b = 3;

    p1: Point = new ColoredPoint.initPoint(1, 2)
    p2: Point = new ColoredPoint.initPoint(2, 1)
    p3: Point = p1.add(p2)

    println(unparseInt(p3.x))
    println(unparseInt(p3.y))
}