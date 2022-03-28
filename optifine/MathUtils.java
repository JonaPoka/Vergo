package optifine;

import java.util.Random;

public class MathUtils
{
    public static float getRandomInRange(float min, float max) {
        Random random = new Random();
        float range = max - min;
        float scaled = random.nextFloat() * range;
        float shifted = scaled + min;
        return shifted;
    }

    public static int getRandomInRange(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static int getAverage(int[] p_getAverage_0_)
    {
        if (p_getAverage_0_.length <= 0)
        {
            return 0;
        }
        else
        {
            int i = 0;

            for (int j = 0; j < p_getAverage_0_.length; ++j)
            {
                int k = p_getAverage_0_[j];
                i += k;
            }

            int l = i / p_getAverage_0_.length;
            return l;
        }
    }
}
