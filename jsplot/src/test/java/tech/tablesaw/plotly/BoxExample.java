/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.plotly;

import org.junit.Ignore;
import org.junit.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BoxTrace;

/**
 * Basic sample box plot. It shows the distribution of the number of people injured in tornadoes, broken out
 * by the scale of the Tornado.
 */
public class BoxExample {

    private final Object[] x = {"sheep", "cows", "fish", "tree sloths", "sheep", "cows", "fish", "tree sloths", "sheep", "cows", "fish", "tree sloths"};
    private final double[] y = {1, 4, 9, 16, 3, 6, 8, 8, 2, 4, 7, 11};

    @Ignore
    @Test
    public void testAsJavascript() {
        BoxTrace trace =
                BoxTrace.builder(x, y)
                        .build();

        System.out.println(trace.asJavascript(1));
    }

    @Ignore
    @Test
    public void show() {

        BoxTrace trace = BoxTrace.builder(x, y).build();

        Figure figure = new Figure(trace);
        Plot.show(figure, "target");
    }

    @Test
    public void test1() throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");

        Layout layout = Layout.builder().title("Tornado Injuries by Scale").build();

        BoxTrace trace = BoxTrace.builder(
                table.categoricalColumn("scale"),
                table.nCol("injuries")).build();
        Plot.show(new Figure(layout, trace));
    }
}
