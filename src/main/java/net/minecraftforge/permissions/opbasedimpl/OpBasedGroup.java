package net.minecraftforge.permissions.opbasedimpl;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
 
import com.google.common.collect.Sets;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.api.Group;
 
public class OpBasedGroup implements Group
{
    private Set<UUID> players = Sets.newHashSet();
    private String name;
    private Group parent;
    
    public OpBasedGroup(String name)
    {
        this.name = name;
    }
    
    @Override
    public void addPlayerToGroup(EntityPlayer player)
    {
        players.add(player.getPersistentID());
    }
 
    @Override
    public boolean removePlayerFromGroup(EntityPlayer player)
    {
        return players.remove(player.getPersistentID());
        
    }
    
    @Override
    public boolean isPlayerInGroup(EntityPlayer player)
    {
        return players.contains(player.getPersistentID());
    }
 
    @Override
    public Collection<UUID> getAllPlayers()
    {
        return players;
    }
    
    @Override
    public void setParent(Group parent)
    {
        this.parent = parent;
    }
    
    @Override
    public Group getParent()
    {
        return parent;
    }
 
    @Override
    public String getName()
    {
        return name;
    }
 
    @Override
    public void setName(String name)
    {
        this.name = name;
    }
}