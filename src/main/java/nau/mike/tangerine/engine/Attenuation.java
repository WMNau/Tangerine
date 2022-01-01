package nau.mike.tangerine.engine;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Attenuation {

  private float constant;
  private float linear;
  private float quadratic;
}
