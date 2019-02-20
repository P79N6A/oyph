import org.junit.*;

import com.shovesoft.sp2p.BaseUnit;

import java.math.BigInteger;
import java.util.*;

import play.test.*;
import models.main.bean.*;

public class BasicTest extends BaseUnit {

    @Test
    public void aVeryImportantThingToTest() {
    	String num_2 = new BigInteger("4").toString(2);
		System.out.println(num_2);
    }

}
