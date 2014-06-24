package net.minecraftforge.permissions.opbasedimpl;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
 
import com.google.common.collect.Sets;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.api.IGroup;
 
public class OpBasedGroup implements IGroup
{
    private Set<UUID> players = Sets.newHashSet();
    private String name;
    private IGroup parent;
    
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
    public void setParent(IGroup parent)
    {
        this.parent = parent;
    }
    
    @Override
    public IGroup getParent()
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