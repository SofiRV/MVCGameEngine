package engine.controller.mappers;

import java.util.ArrayList;

import engine.model.bodies.ports.BodyData;
import engine.model.physics.ports.PhysicsValuesDTO;
import engine.view.renderables.ports.DynamicRenderDTO;

public class DynamicRenderableMapper {

    public static DynamicRenderDTO fromBodyDTO(BodyData bodyData) {
        PhysicsValuesDTO phyValues = bodyData.getPhysicsValues();

        if (phyValues == null || bodyData.entityId == null) {
            return null;
        }

        DynamicRenderDTO renderablesData = new DynamicRenderDTO(
                bodyData.entityId,
                phyValues.posX, phyValues.posY,
                phyValues.angle,
                phyValues.size,
                phyValues.timeStamp,
                phyValues.speedX, phyValues.speedY,
                phyValues.accX, phyValues.accY,
                phyValues.timeStamp,
                bodyData.isVisible());

        return renderablesData;
    }

    public static ArrayList<DynamicRenderDTO> fromBodyDTO(ArrayList<BodyData> bodyData) {
        ArrayList<DynamicRenderDTO> renderableValues = new ArrayList<>();

        for (BodyData bodyDto : bodyData) {
            DynamicRenderDTO renderable = DynamicRenderableMapper.fromBodyDTO(bodyDto);
            renderableValues.add(renderable);
        }

        return renderableValues;
    }

}