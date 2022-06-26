package dev.kosmx.playerAnim.core.data.quarktool;


import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;

public class PartMap {
    public KeyframeAnimation.StateCollection part;
    public PartValue x;
    public PartValue y;
    public PartValue z;

    public PartMap(KeyframeAnimation.StateCollection part){
        this.part = part;
        this.x = new PartValue(this.part.pitch);
        this.y = new PartValue(this.part.yaw);
        this.z = new PartValue(this.part.roll);
    }

    static class PartValue {
        private float value;
        private int lastTick;
        private final KeyframeAnimation.StateCollection.State timeline;


        private PartValue(KeyframeAnimation.StateCollection.State timeline){
            this.timeline = timeline;
        }

        public void addValue(int tick, float value, Ease ease){
            this.lastTick = tick;
            this.timeline.addKeyFrame(tick, value, ease);
        }

        public void addValue(int tickFrom, int tickTo, float value, Ease ease) throws QuarkParsingError{
            if(tickFrom < this.lastTick){
                throw new QuarkParsingError();
            }else if(tickFrom == this.lastTick && timeline.getKeyFrames().size() != 0){
                timeline.replaceEase(timeline.findAtTick(tickFrom), ease);
                //timeline.keyFrames.get(timeline.findAtTick(tickFrom)).ease =ease;
            }else{
                timeline.addKeyFrame(tickFrom, this.value, ease);
            }
            this.value = value;
            this.lastTick = tickTo;
            this.timeline.addKeyFrame(tickTo, this.value, Ease.CONSTANT);
        }

        public float getValue(){
            return value;
        }

        public void hold(){
            //this.timeline.keyFrames.get(this.timeline.keyFrames.size() - 1).setEase(Ease.CONSTANT);
            this.timeline.replaceEase(this.timeline.length() -1, Ease.CONSTANT);
        }

        public void setValue(float valueAfter){
            this.value = valueAfter;
        }
    }

}
