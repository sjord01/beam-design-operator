package sordonez.operator.crd;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("beam.sordonez.com")
@Version("v1")
public class BeamDesign extends CustomResource<BeamDesignSpec, BeamDesignStatus> {
    private static final long serialVersionUID = 1L;
}
