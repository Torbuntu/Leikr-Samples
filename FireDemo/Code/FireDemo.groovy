class FireDemo extends leikr.Engine {
    // note - this whole demo is based off code from https://twitter.com/SVKaiser/status/977968698165202944
    // no license was included, but since it was posted publicly on CodePen, it's MIT: https://blog.codepen.io/legal/licensing/

    // store screen dimensions for easier access
    def sw = 240
    def sh = 160

    // store color entries in a palette
    def rgbs = [
        0x07,0x07,0x07,
        0x1F,0x07,0x07,
        0x2F,0x0F,0x07,
        0x47,0x0F,0x07,
        0x57,0x17,0x07,
        0x67,0x1F,0x07,
        0x77,0x1F,0x07,
        0x8F,0x27,0x07,
        0x9F,0x2F,0x07,
        0xAF,0x3F,0x07,
        0xBF,0x47,0x07,
        0xC7,0x47,0x07,
        0xDF,0x4F,0x07,
        0xDF,0x57,0x07,
        0xDF,0x57,0x07,
        0xD7,0x5F,0x07,
        0xD7,0x5F,0x07,
        0xD7,0x67,0x0F,
        0xCF,0x6F,0x0F,
        0xCF,0x77,0x0F,
        0xCF,0x7F,0x0F,
        0xCF,0x87,0x17,
        0xC7,0x87,0x17,
        0xC7,0x8F,0x17,
        0xC7,0x97,0x1F,
        0xBF,0x9F,0x1F,
        0xBF,0x9F,0x1F,
        0xBF,0xA7,0x27,
        0xBF,0xA7,0x27,
        0xBF,0xAF,0x2F,
        0xB7,0xAF,0x2F,
        0xB7,0xB7,0x2F,
        0xB7,0xB7,0x37,
        0xCF,0xCF,0x6F,
        0xDF,0xDF,0x9F,
        0xEF,0xEF,0xC7,
        0xFF,0xFF,0xFF
    ]

    def palette = []
    def px = []

    void create() {
        // build indexed palette for quicker access
        for (int i in 0..rgbs.size()/3) {
            palette.add([r: rgbs[i * 3 + 0], g: rgbs[i * 3 + 1], b: rgbs[i * 3 + 2]])
        }

        // init pixels
        for (int i in 0..sw*sh) {
            px.add(0)
            // px[i] = 0
        }

        // seed bottom line
        for (int i in 0..sw) {
            px[(sh-1)*sw + i] = 36
        }
    }

    void spreadFire(int src) {
        def pixel = px[src]
        // pixel above is black if current is black
        if (pixel == 0) {
            px[src - sw] = 0
        } else {
            def randIdx = Math.round(Math.random() * 3.0) & 3
            def dst = src - randIdx + 1
            px[dst - sw] = pixel - (randIdx & 1)
        }
    }

    void doFire() {
        for (int x in 0..sw) {
            for (int y in 1..sh-1) {
                spreadFire(y * sw + x)
            }
        }
    }

    // do all this stuff in render() since we're looping through every pixel anyway
    void render() {
        for (int y in 0..sh-1) {
            for (int x in 0..sw) {
                def index = px[y*sw+x]
                // build the RGB color based on the palette entry
                def pixel = palette[index]
                def rgb = "$pixel.r,$pixel.g,$pixel.b"
                
                drawPixel(rgb, x, y)
            }
        }

        // update fire pixels
        doFire()
    }
}