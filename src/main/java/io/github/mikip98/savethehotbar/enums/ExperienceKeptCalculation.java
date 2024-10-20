package io.github.mikip98.savethehotbar.enums;

import net.minecraft.entity.player.PlayerEntity;

/*
MIT License

Copyright (c) 2023 Pneumono_

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
public enum ExperienceKeptCalculation {
    ALL(entity -> entity.totalExperience),
    THREE_QUARTERS(entity -> (int)(entity.totalExperience * 3f / 4f)),
    TWO_THIRDS(entity -> (int)(entity.totalExperience * 2f / 3f)),
    HALF(entity -> (int)(entity.totalExperience / 2f)),
    ONE_THIRD(entity -> (int)(entity.totalExperience / 3f)),
    ONE_QUARTER(entity -> (int)(entity.totalExperience / 4f)),
    VANILLA(entity -> entity.experienceLevel * 7);

    private final Calculation calculation;

    ExperienceKeptCalculation(Calculation calculation) {
        this.calculation = calculation;
    }

    public int calculateExperienceKept(PlayerEntity entity) {
        return this.calculation.calculateExperienceKept(entity);
    }

    public interface Calculation {
        int calculateExperienceKept(PlayerEntity entity);
    }
}
